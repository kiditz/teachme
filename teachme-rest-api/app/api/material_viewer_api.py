from flask import Blueprint, request
from slerp.logger import logging

from service.material_viewer_service import MaterialViewerService

log = logging.getLogger(__name__)

material_viewer_api_blue_print = Blueprint('material_viewer_api_blue_print', __name__)
api = material_viewer_api_blue_print
material_viewer_service = MaterialViewerService()


@api.route('/add_material_viewer', methods=['POST'])
def add_material_viewer():
    """
    {
    "user_id": "Long",
    "material_id": "Long"
    }
    """
    domain = request.get_json()
    return material_viewer_service.add_material_viewer(domain)


@api.route('/count_material_viewer_by_material_id', methods=['GET'])
def count_material_viewer_by_material_id():

    """
    {
        "material_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return material_viewer_service.count_material_viewer_by_material_id(domain)