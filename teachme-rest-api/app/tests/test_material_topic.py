import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestMaterialTopic(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_add_material_topic(self):
		# TODO : Replace All Input POST
		result = requests.post(self.BASE_URL + 'add_material_topic', json={
			"name": "String",
			"user_id": 1
		})
		log.info('test_add_material_topic: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_get_material_topic(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_material_topic', params={
			'page': '1',
			'size': '10'
		})
		log.info('test_get_material_topic: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_get_material_topic_by_level_id(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_material_topic_by_level_id', params={
			'page': '1',
			'size': '10',
			'level_id': '4'
		})
		log.info('test_get_material_topic_by_level_id: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_get_material_topic_by_name(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_material_topic_by_name', params={
			'page': '1',
			'name': 'Matematika',
			'size': '10'
		})
		log.info('test_get_material_topic_by_name: %s', result.json())
		self.assertIs(result.status_code, 200)
		response = result.json()
		self.assertEqual(response['status'], 'OK')
		self.assertIsInstance(response['payload'], list)


if __name__ == '__main__':
	unittest.main()
