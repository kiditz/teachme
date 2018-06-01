from flask import Blueprint, request
from slerp.logger import logging

from service.school_class_service import SchoolClassService

log = logging.getLogger(__name__)

school_class_api_blue_print = Blueprint('school_class_api_blue_print', __name__)
api = school_class_api_blue_print
school_class_service = SchoolClassService()


@api.route('/get_school_class_by_level_id', methods=['GET'])
def get_school_class_by_level_id():
    """
    {
        "level_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return school_class_service.get_school_class_by_level_id(domain)