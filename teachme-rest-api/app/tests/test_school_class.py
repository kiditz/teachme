import unittest
import requests
from slerp.logger import logging
log = logging.getLogger(__name__)

class TestSchoolClass(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"

	def setUp(self):
		pass

	def test_add_school_class(self):
		result = requests.post(self.BASE_URL + 'add_school_class', json={
		    "name": "TK B",
		    "level_id": 2
		})
		log.info("Result %s", result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
		
		
if __name__ == '__main__':
	unittest.main()