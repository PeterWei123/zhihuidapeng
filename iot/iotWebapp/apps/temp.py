tempDataPin = 12   # DHT11传感器数据线

try:
    import Adafruit_DHT
    sensor = Adafruit_DHT.DHT11
    myHTflag = False
except:
    print('temp没有Adafruit_DHT模块')

def tempHumidity():
    try:
        humidity = None
        temperature = None
        global myHTflag
        if not myHTflag:
            humidity, temperature = Adafruit_DHT.read_retry(sensor, tempDataPin)
        if not humidity or not temperature:
            myHTflag = True
            return '0', '0'
        temperature = str(temperature).split('.')[0]  # 字符串 + '℃'
        humidity = str(humidity).split('.')[0]        # 字符串 + '%'
        return temperature, humidity
    except Exception as e:
        print(e)
        return '0', '0'