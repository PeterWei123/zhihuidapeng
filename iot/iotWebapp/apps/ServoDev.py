try:
    import RPi.GPIO as GPIO  # 在树莓派Python环境中直接引入即可使用
except:
    print('舵机没有GPIO模块')
from apps import Utils
import time


class ServoDev:
    def angle(self, angle):
        return not angle or angle < 0 or angle > 180

    def __init__(self, servoPN):
        self.servoPN = servoPN  # 传入19
        self.initialized = False
        mapper = Utils.Json5Mapper(Utils.ServoPath())
        self.close = mapper['close']
        self.open = mapper['open']
        if self.angle(self.close):
            self.close = 0
        if self.angle(self.open):
            self.open = 90
        print('当前|舵机开合角度(' + str(self.close) + '~' + str(self.open) + ')度')

    def devSetup(self):
        if not self.initialized:
            try:
                GPIO.setmode(GPIO.BCM)
                GPIO.setup(self.servoPN, GPIO.OUT)
                self.PWM = GPIO.PWM(self.servoPN, 50)
                self.PWM.start(0)
                self.initialized = True
            except Exception as e:
                print(f"舵机初始化失败 {str(e)}")
                self.initialized = False

    def rotater(self, angle):
        if not self.initialized:
            self.devSetup()
        if not self.initialized:
            print("无法控制舵机，未完成初始化")
            return
        try:  # 标准舵机占空比计算：2.5%~12.5%对应0~180度
            angle = max(0, min(180, angle))
            rate = 2.5 + (angle / 180) * 10
            print(f"旋转到 {str(angle)} 度")
            self.PWM.ChangeDutyCycle(rate)
            # 等待~
            time.sleep(0.6)  # 稍微延长等待时间确保到位
            self.PWM.ChangeDutyCycle(0)  # 停止发送信号，避免舵机抖动
        except Exception as e:
            print(f"旋转过程出错 {str(e)}")
            self.initialized = False  # 标记为未初始化，下次会重新尝试

    def Rotate(self, state):
        try:
            self.rotater(self.open if state == 'open' else self.close)
        except KeyboardInterrupt:
            print("用户中断操作")
