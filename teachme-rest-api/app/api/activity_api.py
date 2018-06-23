from flask import Blueprint, request
from slerp.logger import logging

from service.activity_service import ActivityService

log = logging.getLogger(__name__)

activity_api_blue_print = Blueprint('activity_api_blue_print', __name__)
api = activity_api_blue_print
activity_service = ActivityService()


@api.route('/add_activity', methods=['POST'])
def add_activity():
	"""
	{
	"message": "String",
	"user_id": 1,
	}
	"""
	domain = request.get_json()
	return activity_service.add_activity(domain)


@api.route('/get_activity_by_user_id', methods=['GET'])
def get_activity_by_user_id():
	"""
	{
		"page": 1,
		"size": 10,
		"user_id": 1
	}
	"""
	domain = request.args.to_dict()
	return activity_service.get_activity_by_user_id(domain)


# Getting activity from friend
@api.route('/get_activity', methods=['GET'])
def get_activity():
	"""
	{
		"page": 1,
		"size": 10,
		"user_id": 1
	}
	"""
	domain = request.args.to_dict()
	return activity_service.get_activity(domain)
