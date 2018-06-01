from flask import Blueprint, request
from slerp.logger import logging

from service.material_topic_service import MaterialTopicService

log = logging.getLogger(__name__)

material_topic_api_blue_print = Blueprint('material_topic_api_blue_print', __name__)
api = material_topic_api_blue_print
material_topic_service = MaterialTopicService()


@api.route('/add_material_topic', methods=['POST'])
def add_material_topic():
    """
    {
    "user_id": "Long",
    "name": "String"
    }
    """
    domain = request.get_json()
    return material_topic_service.add_material_topic(domain)


@api.route('/get_material_topic', methods=['GET'])
def get_material_topic():
    """
    {
        "page": "Long",
        "size": "Long"
    }
    """
    domain = request.args.to_dict()
    return material_topic_service.get_material_topic(domain)


@api.route('/get_material_topic_by_level_id', methods=['GET'])
def get_material_topic_by_level_id():
    """
    {
        "page": 1,
        "size": 10,
        "level_id": 4
    }
    """
    domain = request.args.to_dict()
    return material_topic_service.get_material_topic_by_level_id(domain)