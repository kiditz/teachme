from flask import Blueprint, request
from slerp.logger import logging

from service.friend_service import FriendService

log = logging.getLogger(__name__)

friend_api_blue_print = Blueprint('friend_api_blue_print', __name__)
api = friend_api_blue_print
friend_service = FriendService()


@api.route('/add_friend', methods=['POST'])
def add_friend():
	"""
	{
	"user_id": "Long",
	"friend_id": "Long",
	"status": "String"
	}
	"""
	domain = request.get_json()
	return friend_service.add_friend(domain)


@api.route('/get_friend_by_user_id', methods=['GET'])
def get_friend_by_user_id():
	"""
	{
		"page": "Long",
		"size": "Long",
		"user_id": "Long"
	}
	"""
	domain = request.args.to_dict()
	return friend_service.get_friend_by_user_id(domain)


@api.route('/delete_friend', methods=['DELETE'])
def delete_friend():
	domain = request.args.to_dict()
	return friend_service.delete_friend(domain)


@api.route("/get_friends", methods=['GET'])
def get_friends():
	domain = request.args.to_dict()
	return friend_service.get_friends(domain)


@api.route("/count_following_followers", methods=['GET'])
def count_following_followers():
	domain = request.args.to_dict()
	return friend_service.count_following_followers(domain)