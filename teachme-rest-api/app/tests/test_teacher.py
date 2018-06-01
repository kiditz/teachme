import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestTeacher(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_find_teacher_by_username(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'find_teacher_by_username', params={
			'username': 'kiditz'
		})
		log.info('test_find_teacher_by_username : %s ', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
		self.assertIsNotNone(result.json()['payload'])


if __name__ == '__main__':
	unittest.main()
