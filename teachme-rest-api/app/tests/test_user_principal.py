import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestUserPrincipal(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_find_user_principal_by_username(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'find_user_principal_by_username', params={
			'username': 'kiditz'
		})
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_get_image(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_image', params={
			'username': 'kiditz'
		})
		self.assertIs(result.status_code, 200)
	
	# self.assertEqual(result.json()['status'], 'OK')
	
	def test_edit_user_principal_by_username(self):
		# TODO : Replace All Input PUT
		result = requests.put(self.BASE_URL + 'edit_user_principal_by_username', json={
			"id": 1,
			"phone_number": "String",
			"username": "kiditz",
			"fullname": "String",
			"gender": "L",
		})
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')


if __name__ == '__main__':
	unittest.main()
