from flask import Blueprint, request
from slerp.logger import logging
from slerp import sender
from service.task_service import TaskService

log = logging.getLogger(__name__)

task_api_blue_print = Blueprint('task_api_blue_print', __name__)
api = task_api_blue_print
task_service = TaskService()


@api.route('/add_task', methods=['POST'])
def add_task():
	"""
	{
	"title": "String",
	"user_id": "Long",
	"active": "Boolean"
	}
	"""
	domain = request.get_json()
	return task_service.add_task(domain)


@api.route('/add_task_answer', methods=['POST'])
def add_task_answer():
	"""
	{
		"task_id": "Long",
		"user_id": "Long",
		"answers": [
			{
				"answer": "String",
				"question_id": "Long"
			}
		]
	}
	"""
	domain = request.get_json()
	return task_service.add_task_answer(domain)


@api.route('/get_task_by_user_id', methods=['GET'])
def get_task_by_user_id():
	"""
	{
		"page": "Long",
		"size": "Long",
		"user_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return task_service.get_task_by_user_id(domain)


@api.route('/get_task_by_user_group', methods=['GET'])
def get_task_by_user_group():
	"""
	{
		"page": "Long",
		"size": "Long",
		"user_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return task_service.get_task_by_user_group(domain)


@api.route('/get_user_in_task', methods=['GET'])
def get_user_in_task():
	"""
	{
		"page": "Long",
		"size": "Long",
		"user_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return task_service.get_user_in_task(domain)

@api.route('/get_task_question', methods=['GET'])
def get_task_question():
	"""
	{
		"task_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return task_service.get_task_question(domain)


@api.route('/test_message')
def test_message():
	data = {'test': "This is test roger."}
	sender.send_message('task', data, 0)
	return {'payload': True}