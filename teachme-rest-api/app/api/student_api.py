from flask import Blueprint, request
from slerp.logger import logging

from service.student_service import StudentService

log = logging.getLogger(__name__)

student_api_blue_print = Blueprint('student_api_blue_print', __name__)
api = student_api_blue_print
student_service = StudentService()


@api.route('/find_student_by_user_id', methods=['GET'])
def find_student_by_user_id():

    """
    {
        "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return student_service.find_student_by_user_id(domain)