from flask import Blueprint, request
from slerp.logger import logging

from service.lesson_service import LessonService

log = logging.getLogger(__name__)

lesson_api_blue_print = Blueprint('lesson_api_blue_print', __name__)
api = lesson_api_blue_print
lesson_service = LessonService()


@api.route('/add_lesson', methods=['POST'])
def add_lesson():
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
	return lesson_service.add_lesson(domain)


@api.route('/get_lesson_by_title', methods=['GET'])
def get_lesson_by_title():
	"""
	{
		"page": 1,
		"size": 10,
		"title": "String"
	}
	"""
	domain = request.args.to_dict()
	return lesson_service.get_lesson_by_title(domain)


@api.route('/get_lesson_by_topic_id', methods=['GET'])
def get_lesson_by_topic_id():
	"""
	{
		"page": 1,
		"size": 10,
		"topic_id": 4
	}
	"""
	domain = request.args.to_dict()
	return lesson_service.get_lesson_by_topic_id(domain)


@api.route('/get_lesson_image', methods=['GET'])
def get_lesson_image():
	"""
    {
        "id": 15
    }
    """
	domain = request.args.to_dict()
	return lesson_service.get_lesson_image(domain)


@api.route('/get_lesson_by_user_id', methods=['GET'])
def get_lesson_by_user_id():
	"""
    {
        "page": "Long",
        "size": "Long",
        "user_id": "Long"
    }
    """
	domain = request.args.to_dict()
	return lesson_service.get_lesson_by_user_id(domain)


@api.route('/find_lesson_by_id', methods=['GET'])
def find_lesson_by_id():
	"""
    {
        "id": "Long"
    }
    """
	domain = request.args.to_dict()
	return lesson_service.find_lesson_by_id(domain)


@api.route('/activate_lesson', methods=['PUT'])
def activate_lesson():
	"""
    {
    "id": 51,
    "active": "A"
    }
    """
	domain = request.get_json()
	return lesson_service.activate_lesson(domain)