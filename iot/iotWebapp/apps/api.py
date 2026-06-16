servoPin = 19

try:
    import RPi.GPIO as GPIO
    GPIO.setmode(GPIO.BCM)
except:
    print('api没有GPIO模块')
from flask import redirect, Blueprint, request
from flask_cors import cross_origin
from apps.ServoDev import ServoDev
from apps import Utils
import json

Servo = ServoDev(servoPin)

# api模块：是java的接口集合！
api = Blueprint('api', __name__, url_prefix='/api')


@api.route('/javaip')
@cross_origin()
def javaip():
    javaip = request.args.get('javaip')
    if not javaip.startswith('http'):
        javaip = 'http://' + javaip
    path = Utils.DbPath().replace('db.json', 'javaip.txt')
    with Utils.Writer(path) as f:
        f().write(javaip)
    myport = request.args.get('myport')
    if myport:
        path = Utils.DbPath().replace('db.json', 'localPort.txt')
        with Utils.Writer(path) as f:
            f().write(myport)
    mqttTime = request.args.get('mqttTime')
    if mqttTime:
        path = Utils.DbPath().replace('db.json', 'initMqtt.txt')
        with Utils.Writer(path) as f:
            try:
                mqttTime = int(mqttTime)
                f().write(str(mqttTime))
            except Exception as e:
                print(e)
                f().write('5')
    auto = request.args.get('auto')
    if auto:
        ledAuto(auto)
    return redirect('/sysmgr/appip')


@api.route('/ledAuto/<auto>')
@cross_origin()  # /ledAuto/reverse
def ledAuto(auto):
    if auto != '手动' and auto != '自动':
        auto = Utils.ReadAuto()
        auto = '手动' if not auto or auto == '自动' else '自动'
    Utils.WriteAuto(auto)
    return auto


@api.route('/servo/<close>/<open>')
@cross_origin()
def servo(close, open):
    data = json.dumps({
        'open': int(open),
        'close': int(close)
    })
    with Utils.Writer(Utils.ServoPath()) as f:
        f().write(data)
    return redirect('/sysmgr/servo')


# 树莓派Server内部测试地址，请不要访问
@api.route('/sensor')
@cross_origin()
def sensor():
    return request.args.get('data')


@api.route('/dev')
@cross_origin()
def dev():
    return Utils.Json5Mapper(Utils.VtlDataPath())


# app.py
def vtl_ctl(option, pin):
    path = Utils.VtlDataPath()
    msg = '打开' if option == 'open' else '关闭'
    data = Utils.Json5Mapper(path)
    mapdict = {
        19: 'greenhouse',  # 大棚顶棚
        16: 'water',  # 水肥机
        20: 'led',  # 补光灯
        21: 'fan',  # 排风机
    }
    data[mapdict[pin]] = msg
    if msg == '打开' and pin == 16:
        data[mapdict[21]] = '关闭'
    elif msg == '打开' and pin == 21:
        data[mapdict[16]] = '关闭'
    with Utils.Writer(path) as f:
        f().write(json.dumps(data))
    return msg


def controller(option, pin):
    GPIO.setup(pin, GPIO.OUT)  # 输出电平模式
    GPIO.output(pin, GPIO.HIGH if option == 'open' else GPIO.LOW)
    if option == 'open' and pin == 16:  ## 为了确保电流较小 ##
        GPIO.setup(21, GPIO.OUT)
        GPIO.output(21, GPIO.LOW)
    elif option == 'open' and pin == 21:
        GPIO.setup(16, GPIO.OUT)
        GPIO.output(16, GPIO.LOW)


@api.route('/gpio/<option>/<pin>')
@cross_origin()
def gpio(option, pin='20'):  # 20引脚必须接led
    pin = int(pin)
    if pin not in [19, 16, 20, 21]:
        return json.dumps({
            'code': 0,
            'option': '操作无效',
            'msg': 'pin引脚号查看一下是否写错'
        })
    msg = vtl_ctl(option, pin)
    try:
        if pin == servoPin:
            Servo.Rotate(option)
        else:
            controller(option, pin)
        port = request.environ.get('SERVER_PORT')
        return json.dumps({
            'code': 1,
            'option': msg,
            'msg': '成功' + msg + '引脚' + str(pin) + ' 请查看硬件',
            'url': f"{Utils.IP()}:{port}/static/vtldata.json"
        })
    except:
        return json.dumps({
            'code': 0,
            'option': msg,
        })