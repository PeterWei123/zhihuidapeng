from flask import Flask, redirect, render_template, request
from apps.sysmgr import sysmgr
from apps import Utils
from apps.api import api
from apps.user import user
from datetime import datetime
import paho.mqtt.client
import apps.lm393
import threading
import apps.temp
import random
import json
import time


def index():
    device, timer, tc_host = connecting()
    number = Utils.Step - 1
    while True:
        number = core(number, device, timer, tc_host)


def connecting():
    with Utils.Reader(Utils.DbPath().replace('db.json', 'initMqtt.txt')) as f:
        initMqttTime = f().read()
    print('初始化mqtt时间：' + initMqttTime)
    data = Utils.Json5Mapper(Utils.DbPath())
    timer = data['TIMER']
    username = data['USERNAME']
    print('时间：' + str(timer) + 's ---- 账号：' + str(data))
    if not username:  # 主要判断USERNAME
        print('缺少mqtt参数连不上云平台')
        return None, timer
    tc_host = data['TC_HOST']
    password = data['PASSWORD']
    client_id = data['CLIENT_ID']
    device = paho.mqtt.client.Client(client_id=client_id)
    device.on_connect = Utils.MyConnect
    if 'mqtt' in tc_host:
        device.on_publish = Utils.MyConnect
    device.username_pw_set(username, password)
    fail_num = 0
    while True:
        try:
            device.connect(host=tc_host, port=1883, keepalive=60)
            device.loop_start()
            print('启动mqtt通信心跳检查机制')
            break
        except Exception as e:
            device.loop_stop()
            device.disconnect()
            print(f"连接云平台异常|重连 {str(e)}")
            time.sleep(1)
            fail_num += 1
            if fail_num == int(initMqttTime):
                print('请检查服务器是否断网')
                break
    return device, timer, tc_host


def core(number, device, timer, tc_host):
    def ifelse():
        return random.randint(0, 1)
    def rdom(start, end):
        return str(random.randint(start, end))
    try:
        suns = apps.lm393.sunStatus()
        soil = apps.lm393.soilStatus()
        temp = apps.temp.tempHumidity()
        rnum = False
    except Exception as e:
        f"{str(e)}"
        rnum = True
        first = rdom(10, 30)
        second = rdom(30, 80)
        temp = (first, second)
        soil = '土壤干燥' if ifelse() else '土壤湿润'
        flag = int(rdom(0, 2)) == 1
        suns = '黑夜/阴雨' if flag else '白天/强光'
    air = ''.join(str(temp).split())
    replace = { "'": '', ',': '℃,', ')': '%)' }
    for k, v in replace.items():
        air = air.replace(k, v)
    greenhouse = ('虚拟' if rnum else '真实') + '大棚'
    data = greenhouse + '-' + suns + '-' + soil + '-' + air
    print(data)
    mystamp = datetime.now()
    number += 1
    if number != Utils.Step:
        half_step = Utils.Step / 2
        time.sleep(timer)
        if number == half_step:
            httpserver(data, timer, rnum, mystamp.timestamp())
        return number
    if rnum and 'mqtt' in tc_host:
        print('虚拟大棚不发送数据到云平台')
        time.sleep(timer)
        httpserver(data, timer, rnum, mystamp.timestamp())
        return 0
    try:
        fmt_date = mystamp.strftime('%d日 %H:%M:%S')
        device.publish('attributes', json.dumps({
            'humidity': int(temp[1]),
            'temperature': int(temp[0]),
            'soilHumidity': soil,
            'sunLight': suns,
            'timestamp': fmt_date
        }), qos=1).wait_for_publish(timeout=timer)
        message = 'mqtt成功发送数据到云平台'
        print(f"\033[31m{message}\033[0m" if rnum else message)
    except Exception as e:
        print(f"mqtt无法通信|异常 {str(e)}")
        time.sleep(timer)
    httpserver(data, timer, rnum, mystamp.timestamp())
    return 0


def httpserver(data, timer, rnum, mystamp):
    java_path = Utils.DbPath().replace('db.json', 'javaip.txt')
    port_path = Utils.DbPath().replace('db.json', 'localPort.txt')
    vtl_data = Utils.Json5Mapper(Utils.VtlDataPath())
    auto = Utils.ReadAuto()
    led2 = '控灯'
    if auto == '自动':
        suns = data.split('-')[1]
        opts = 'open' if suns == '黑夜/阴雨' else 'close'
        led2 = leds(opts, timer, vtl_data['led'])  # 打开/开灯
        vtl_data['led'] = '打开' if led2 == '开灯' else '关闭'
    with Utils.Reader(java_path) as f:
        ip = f().read()
    with Utils.Reader(port_path) as f:
        myport = f().read()
    if not ip:
        ip = '127.0.0.1:' + (myport if rnum else '8081')
    myip = 'http://' + Utils.IP() + ':8081'
    if data.startswith('虚拟'):
        myip = 'http://127.0.0.1:' + myport
    try:  # 大棚顶棚、水肥机、补光灯、排风机
        params = ['greenhouse', 'water', 'led', 'fan']
        data += '-' + str([
            '已' + vtl_data[params[0]],
            '已' + vtl_data[params[1]],
            '已' + vtl_data[params[2]],
            '已' + vtl_data[params[3]],
        ]).replace(' ', '').replace("'", '')
        mystamp = str(mystamp * 1000).split('.')[0]
        data += '-' + auto + led2 + '-' + mystamp + '-' + myip
        Utils.ApiSensorRequest(ip, data)
    except Exception as e:
        print(f"{ip} {str(e)}")


def leds(option, timer, led2):
    led2 = '开灯' if led2 == '打开' else '关灯'
    try:
        curr_time = int(datetime.now().timestamp())
        if option == 'open':
            Utils.WriteTime(datetime.now().timestamp())
            apps.api.vtl_ctl(option, 20)
            led2 = '开灯'
            apps.api.controller(option, 20)
        elif curr_time - Utils.ReadTime() > timer * Utils.Step:
            apps.api.vtl_ctl(option, 20)
            led2 = '关灯'
            apps.api.controller(option, 20)
    except:
        print(f"变量auto处在自动开关灯状态")
    return led2


def init_device():
    vtl_path = Utils.VtlDataPath()
    vtl_data = Utils.Json5Mapper(vtl_path)
    vtl_data['led'] = '关闭'
    vtl_data['fan'] = '关闭'
    vtl_data['water'] = '关闭'
    with Utils.Writer(vtl_path) as f:
        f().write(json.dumps(vtl_data))


app = Flask(__name__)
# 初始化dev数据
init_device()
threading.Thread(target=index).start()
app.register_blueprint(sysmgr)
app.register_blueprint(user)
app.register_blueprint(api)


@app.route('/')
def hello_world():
    return redirect('/static/login.html')


@app.route('/main/<username>')
def main(username):
    return render_template('main.html', username=username)


@app.route('/startMqtt')
def start_mqtt():
    data = json.dumps({
        'TIMER': int(request.args.get('TIMER')),
        'USERNAME': request.args.get('USERNAME'),
        'CLIENT_ID': request.args.get('CLIENT_ID'),
        'PASSWORD': request.args.get('PASSWORD'),
        'TC_HOST': request.args.get('TC_HOST')
    })
    with Utils.Writer(Utils.DbPath()) as f:
        f().write(data)
    return '操作成功 [重启/插拔 树莓派电源]'


if __name__ == '__main__':
    # 用vscode/命令启动端口号为8081
    # 用pycharm启动flask端口号为5000
    app.run(host='0.0.0.0', port=8081)
