import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestMaterial(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_add_material_with_new_topic(self):
		result = requests.post(self.BASE_URL + 'add_material', json={
			"description": "Material Description",
			"document_id": "4",
			"type": "video",
			"user_id": "1",
			"price": 0,
			"title": "Material Title",
			"name": "Test Material Topic"
		})
		log.info('test_add_material_with_new_topic: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_add_material_with_existing_topic(self):
		result = requests.post(self.BASE_URL + 'add_material', json={
			"description": "Material Description",
			"document_id": "4",
			"type": "video",
			"user_id": "1",
			"price": 0,
			"title": "Material Title",
			"topic_id": 15
		})
		log.info('test_add_material_with_existing_topic: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_add_material_with_user_not_found(self):
		result = requests.post(self.BASE_URL + 'add_material', json={
			"description": "Material Description",
			"document_id": "4",
			"type": "video",
			"user_id": 1000,
			"price": 0,
			"title": "Material Title",
			"topic_id": 15
		})
		log.info('test_add_material_with_existing_topic: %s', result.json())
		self.assertIs(result.status_code, 200)
		response = result.json()
		self.assertEqual(response['status'], 'FAIL')
		self.assertEqual(response['message'], 'user.not.found')


if __name__ == '__main__':
	unittest.main()
