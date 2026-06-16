import os
import json
import socket
import urllib.request

Step = 4

class MyFile:
    def __init__(self, filename, mode='r'):
        self.file = open(filename, mode, encoding='utf-8')
    def __enter__(self):
        return self
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.file.close()
    def __call__(self, *args, **kwargs):
        return self.file

class Writer(MyFile):
    def __init__(self, filename):
        super().__init__(filename, 'w')

class Reader(MyFile):
    def __init__(self, filename):
        super().__init__(filename, 'r')


def IP():
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
            s.connect(('8.8.8.8', 80))
            ip = s.getsockname()[0]
        return ip
    except Exception as e:
        return '127.0.0.1'


def MyConnect(client, userdata, flags, rc):
    if rc != 0:
        print('[TC]mqtt设备上报频率过快')


def Path(newPath):
    path = os.path.realpath(__file__)
    path = path.replace('\\', '/')
    return path.replace('apps/Utils.py', newPath)


def DbPath():
    return Path('db.json')


def ServoPath():
    return Path('servo.json')


def VtlDataPath():
    return Path('static/vtldata.json')


def Json5Mapper(path):
    with open(path, 'r', encoding='utf-8') as f:
        data = f.read()
    return json.loads(data)


def ReadTime():
    with Reader(Path('time.vue')) as f:
        time = f().read()
    return int(time) if time else 0


def ReadAuto():
    with Reader(Path('ledAuto.txt')) as f:
        ret = f().read()
    return ret if ret else '手动'


def WriteTime(time):
    with Writer(Path('time.vue')) as f:
        f().write(str(int(time)))
    return time


def WriteAuto(ret):
    with Writer(Path('ledAuto.txt')) as f:
        f().write(ret)
    return ret


def Request(url):
    urllib.request.urlopen(url=url, timeout=3)


def ApiSensorRequest(ip, data):
    if not ip.startswith('http://'):
        ip = 'http://' + ip
    print(ip + '/api/sensor?data=' + data)
    Request(ip + '/api/sensor?data=' + urllib.request.quote(data))
