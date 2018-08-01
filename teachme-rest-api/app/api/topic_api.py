from flask import Blueprint, request
from slerp.logger import logging

from service.material_topic_service import MaterialTopicService

log = logging.getLogger(__name__)

topic_api_blue_print = Blueprint('topic_api_blue_print', __name__)
api = topic_api_blue_print
material_topic_service = MaterialTopicService()


@api.route('/add_lesson_topic', methods=['POST'])
def add_lesson_topic():
	"""
	{
	"user_id": 1,
	"name": "String"
	}
	"""
	domain = request.get_json()
	return material_topic_service.add_lesson_topic(domain)


@api.route('/get_lesson_topic', methods=['GET'])
def get_lesson_topic():
	"""
	{
		"page": 1,
		"size": 10,
		"name": "Matematika",
		"level_id": 4
	}
	"""
	domain = request.args.to_dict()
	return material_topic_service.get_lesson_topic(domain)
