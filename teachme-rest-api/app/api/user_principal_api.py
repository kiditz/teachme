from flask import Blueprint, request
from slerp.logger import logging

from service.user_principal_service import UserPrincipalService

log = logging.getLogger(__name__)

user_principal_api_blue_print = Blueprint('user_principal_api_blue_print', __name__)
api = user_principal_api_blue_print
user_principal_service = UserPrincipalService()


@api.route('/register', methods=['POST'])
def register():
	"""
		{
		"phone_number": "087788044374",
		"username": "kiditz",
		"fullname": "Rifky Aditya Bastara",
		"gender": "L",
		"password": "rioters7",
		"level_id": 1,
		"class_id": 1,
		"is_teacher": true
		}
	"""
	domain = request.get_json()
	return user_principal_service.register(domain)


@api.route("/find_user_principal_by_username", methods=['GET'])
def find_user_principal_by_username():
	domain = request.args.to_dict()
	return user_principal_service.find_user_principal_by_username(domain)


@api.route("/find_user_principal_by_id", methods=['GET'])
def find_user_principal_by_id():
	domain = request.args.to_dict()
	return user_principal_service.find_user_principal_by_id(domain)


@api.route('/get_user_image', methods=['GET'])
def get_user_image():
	"""
	{
		"username": "kiditz"
	}
	"""
	domain = request.args.to_dict()
	return user_principal_service.get_user_image(domain)


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
