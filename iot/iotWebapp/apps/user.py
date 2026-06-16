from flask import redirect, Blueprint

user = Blueprint('user', __name__, url_prefix='/user')


# 登录
@user.route('/login', methods=['GET', 'POST'])
def login():
    return redirect('/main/iotAdmin')
