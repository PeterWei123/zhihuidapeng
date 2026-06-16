from flask import Blueprint, render_template, request
try:
    from pypinyin import pinyin, Style
except:
    print('没有安装pypinyin')

from apps import Utils
import json
import os

sysmgr = Blueprint('sysmgr', __name__, url_prefix='/sysmgr')


@sysmgr.route('/servo')
def servo():
    mapper = Utils.Json5Mapper(Utils.ServoPath())
    return render_template('servoMgr.html',
                           close=mapper['close'],
                           open=mapper['open'])


@sysmgr.route('/appip')
def appip():
    path = Utils.DbPath().replace('db.json', 'javaip.txt')
    with Utils.Reader(path) as f:
        javaip = f().read()
    auto = Utils.ReadAuto()
    path = Utils.DbPath().replace('db.json', 'localPort.txt')
    with Utils.Reader(path) as f:
        myport = f().read()
    path = Utils.DbPath().replace('db.json', 'initMqtt.txt')
    with Utils.Reader(path) as f:
        mqttTime = f().read()
    return render_template('appip.html',
                           javaip=javaip,
                           auto=auto,
                           myport=myport,
                           mqttTime=mqttTime)


@sysmgr.route('/mqtt')
def mqtt():
    mapper = Utils.Json5Mapper(Utils.DbPath())
    return render_template('mqttMgr.html',
                           TIMER=mapper['TIMER'],
                           TC_HOST=mapper['TC_HOST'],
                           USERNAME=mapper['USERNAME'],
                           CLIENT_ID=mapper['CLIENT_ID'],
                           PASSWORD=mapper['PASSWORD'])


@sysmgr.route('/wifi')
def wifi():
    return render_template('wifiMgr.html')


@sysmgr.route('/dev')
def dev():
    data = Utils.Json5Mapper(Utils.VtlDataPath())
    path = Utils.DbPath().replace('db.json', 'localPort.txt')
    with Utils.Reader(path) as f:
        myport = f().read()
    led = data['led']
    fan = data['fan']
    water = data['water']
    greenhouse = data['greenhouse']
    return render_template('vtldata.html',
                           myport=myport,
                           led=led,fan=fan,
                           water=water,
                           greenhouse=greenhouse)


@sysmgr.route('/downfile')
def downfile():
    try:
        number = 98
        content = ''
        for name in request.args.get('nameList').split(','):
            name = ''.join([''.join(x) for x in pinyin(
                name.strip(),
                style=Style.NORMAL
            )])
            network = json.dumps({
                'ssid': 'pc' + name.capitalize(),
                'psk': '88888888', 'priority': number
            })
            replace = {
                ': ': '=',
                ', ': '\n',
                '"psk"': '\tpsk',
                '"ssid"': '\n\tssid',
                '"priority"': '\tpriority',
                '{': 'network={',
                '}': '\n}\n',
            }
            for k, v in replace.items():
                network = network.replace(k, v)
            content += network
            number -= 1
            if number < 0:
                break
        path = Utils.DbPath().replace('db.json', '')
        with Utils.Reader(path + 'static/wifitpl.txt') as f:
            data = f().read()
        with Utils.Writer(path + 'static/wpa_supplicant.conf') as f:
            f().write(data + content)
    except:
        pass
    return '操作成功'


@sysmgr.route('/apidoc')
def apidoc():
    return render_template('apidoc.html', apiImage='/static/apidoc.jpg')


@sysmgr.route('/logo', methods=['POST'])
def logo():
    f = request.files['file']
    curpath = os.path.dirname(__file__)
    curpath = curpath.replace('\\apps', '') + '/static/apidoc.jpg'
    f.save(curpath)
    return '/static/apidoc.jpg'