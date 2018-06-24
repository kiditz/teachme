from flask import Blueprint, request
from slerp.logger import logging

from service.material_service import MaterialService

log = logging.getLogger(__name__)

material_api_blue_print = Blueprint('material_api_blue_print', __name__)
api = material_api_blue_print
material_service = MaterialService()


@api.route('/add_material', methods=['POST'])
def add_material():
	"""
	{
	"title": "Material Title",
	"description": "Material Description",
	"type": "video",
	"document_id": 4,
	"user_id": 1,
	"name": "Test Material Topic"
	}
	"""
	domain = request.get_json()
	return material_service.add_material(domain)


@api.route('/get_material_by_title', methods=['GET'])
def get_material_by_title():
	"""
	{
		"page": 1,
		"size": 10,
		"title": "String"
	}
	"""
	domain = request.args.to_dict()
	return material_service.get_material_by_title(domain)


@api.route('/get_material_by_topic_id', methods=['GET'])
def get_material_by_topic_id():
	"""
	{
		"page": 1,
		"size": 10,
		"topic_id": 4
	}
	"""
	domain = request.args.to_dict()
	return material_service.get_material_by_topic_id(domain)


@api.route('/get_material_image', methods=['GET'])
def get_material_image():
	"""
    {
        "id": 15
    }
    """
	domain = request.args.to_dict()
	return material_service.get_material_image(domain)


@api.route('/get_material_by_user_id', methods=['GET'])
def get_material_by_user_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return material_service.get_material_by_user_id(domain)


@api.route('/find_material_by_id', methods=['GET'])
def find_material_by_id():

    """
    {
        "id": "Long"
    }
    """
    domain = request.args.to_dict()
    return material_service.find_material_by_id(domain)