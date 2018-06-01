import unittest

import requests


class TestSchool(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_add_school(self):
		# TODO : Replace All Input POST
		result = requests.post(self.BASE_URL + 'add_school', json={
			"name": "String",
			"description": "String",
			"document_id": 2,
			"url": "String",
			"user_id": 1,
			"address": "String"
		})
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	
	def test_get_school_by_name(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_school_by_name', params={
			'page': 1,
			'size': 10,
			'name': ''
		})
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')


if __name__ == '__main__':
	unittest.main()
