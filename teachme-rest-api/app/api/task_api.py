from flask import Blueprint, request
from slerp.logger import logging

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

@api.route('/get_task_question', methods=['GET'])
def get_task_question():
	"""
	{		
		"task_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return task_service.get_task_question(domain)	
