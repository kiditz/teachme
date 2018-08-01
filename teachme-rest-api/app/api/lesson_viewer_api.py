from flask import Blueprint, request
from slerp.logger import logging

from service.lesson_viewer_service import LessonViewerService

log = logging.getLogger(__name__)

lesson_viewer_api_blue_print = Blueprint('lesson_viewer_api_blue_print', __name__)
api = lesson_viewer_api_blue_print
lesson_viewer_service = LessonViewerService()


@api.route('/add_lesson_viewer', methods=['POST'])
def add_lesson_viewer():
    """
    {
    "user_id": "Long",
    "lesson_id": "Long"
    }
    """
    domain = request.get_json()
    return lesson_viewer_service.add_lesson_viewer(domain)


@api.route('/count_lesson_viewer_by_lesson_id', methods=['GET'])
def count_lesson_viewer_by_lesson_id():

    """
    {
        "lesson_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return lesson_viewer_service.count_lesson_viewer_by_lesson_id(domain)