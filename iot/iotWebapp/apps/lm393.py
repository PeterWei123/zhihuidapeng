sunDoPin = 7    # 7为光敏传感器Do
soilDoPin = 1   # 1为土壤湿度传感器Do

try:
    import RPi.GPIO as GPIO  # 在树莓派Python环境中直接引入即可使用
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(sunDoPin, GPIO.IN)
    GPIO.setup(soilDoPin, GPIO.IN)
except:
    print('lm393没有GPIO模块')

# 高电平 = 干燥，低电平 = 湿润；电位器，需要测试和不断调节
def soilStatus():
    return '土壤干燥' if GPIO.input(soilDoPin) == GPIO.HIGH else '土壤湿润'

def sunStatus():
    return '黑夜/阴雨' if GPIO.input(sunDoPin) == GPIO.HIGH else '白天/强光'