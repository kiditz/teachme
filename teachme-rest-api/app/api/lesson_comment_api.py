from flask import Blueprint, request
from slerp.logger import logging

from service.lesson_comment_service import MaterialCommentService

log = logging.getLogger(__name__)

lesson_comment_api_blue_print = Blueprint('lesson_comment_api_blue_print', __name__)
api = lesson_comment_api_blue_print
lesson_comment_service = MaterialCommentService()


@api.route('/get_lesson_comment_by_sender_user_id', methods=['GET'])
def get_lesson_comment_by_sender_user_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "sender_user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return lesson_comment_service.get_lesson_comment_by_sender_user_id(domain)


@api.route('/get_lesson_comment_by_lesson_id', methods=['GET'])
def get_lesson_comment_by_lesson_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "lesson_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return lesson_comment_service.get_lesson_comment_by_lesson_id(domain)