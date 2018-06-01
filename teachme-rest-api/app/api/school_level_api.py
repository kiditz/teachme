from flask import Blueprint, request
from slerp.logger import logging

from service.school_level_service import SchoolLevelService

log = logging.getLogger(__name__)

school_level_api_blue_print = Blueprint('school_level_api_blue_print', __name__)
api = school_level_api_blue_print
school_level_service = SchoolLevelService()


@api.route('/get_school_level', methods=['GET'])
def get_school_level():
	"""
    {
    }
    """
	domain = request.args.to_dict()
	return school_level_service.get_school_level(domain)
