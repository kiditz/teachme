from flask import Blueprint, request
from slerp.logger import logging

from service.teacher_service import TeacherService

log = logging.getLogger(__name__)

teacher_api_blue_print = Blueprint('teacher_api_blue_print', __name__)
api = teacher_api_blue_print
teacher_service = TeacherService()


@api.route('/find_teacher_by_username', methods=['GET'])
def find_teacher_by_username():

    """
    {
        "username": "kiditz"
    }
    """
    domain = request.args.to_dict()
    return teacher_service.find_teacher_by_username(domain)