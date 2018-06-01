from flask import Blueprint, request
from slerp.logger import logging

from service.school_service import SchoolService

log = logging.getLogger(__name__)

school_api_blue_print = Blueprint('school_api_blue_print', __name__)
api = school_api_blue_print
school_service = SchoolService()


@api.route('/add_school', methods=['POST'])
def add_school():
    """
    {
    "name": "String",
    "description": "String",
    "document_id": 2,
    "url": "String",
    "user_id": 1,
    "address": "String"
    }
    """
    domain = request.get_json()
    return school_service.add_school(domain)


@api.route('/get_school_by_name', methods=['GET'])
def get_school_by_name():

    """
    {
        "page": 1,
        "size": 10,
        "name": "String"
    }
    """
    domain = request.args.to_dict()
    return school_service.get_school_by_name(domain)


@api.route('/find_school_by_id', methods=['GET'])
def find_school_by_id():
    """
	{
		"school_id": 2
	}
	"""
    domain = request.args.to_dict()
    return school_service.find_school_by_id(domain)