from flask import Blueprint, request
from slerp.logger import logging

from service.user_principal_service import UserPrincipalService

log = logging.getLogger(__name__)

user_principal_api_blue_print = Blueprint('user_principal_api_blue_print', __name__)
api = user_principal_api_blue_print
user_principal_service = UserPrincipalService()


@api.route('/register_teacher', methods=['POST'])
def register_teacher():
    """
    {
    "phone_number": "String",
    "username": "string",
    "fullname": "String",
    "gender": "L",
    "password": "String"
    "level_id": 4,
    "class_id": 2
    }
    """
    domain = request.get_json()
    return user_principal_service.register_teacher(domain)


@api.route('/register_student', methods=['POST'])
def register_student():
    """
    {
    "phone_number": "String",
    "username": "string",
    "fullname": "String",
    "gender": "P",
    "password": "String"
    "level_id": 4,
    "class_id": None
    }
    """
    domain = request.get_json()
    return user_principal_service.register_student(domain)


@api.route('/find_user_principal_by_username', methods=['GET'])
def find_user_principal_by_username():

    """
    {
        "username": "kiditz"
    }
    """
    domain = request.args.to_dict()
    return user_principal_service.find_user_principal_by_username(domain)


@api.route('/get_image', methods=['GET'])
def get_image():

    """
    {
        "username": "kiditz"
    }
    """
    domain = request.args.to_dict()
    return user_principal_service.get_image(domain)    


@api.route('/edit_user_principal_by_username', methods=['PUT'])
def edit_user_principal_by_username():
    """
    {    
    "phone_number": "String",
    "username": "kiditz",
    "fullname": "String",
    "gender": "L"
    }
    """
    domain = request.get_json()
    return user_principal_service.edit_user_principal_by_username(domain)



