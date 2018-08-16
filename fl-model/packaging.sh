source bin/activate
python3 setup.py sdist
rm ../teachme-rest-api/fl-model-0.0.1.tar.gz
rm ../task-service/fl-model-0.0.1.tar.gz
cp dist/fl-model-0.0.1.tar.gz ../teachme-rest-api
cp dist/fl-model-0.0.1.tar.gz ../task-service
cd ../teachme-rest-api
source bin/activate
echo 'Install to teachme-rest-api'
pip3 uninstall -y fl-model
pip3 install --no-cache-dir --no-index --find-links fl-model-0.0.1.tar.gz fl-model
cd ../task-service
source bin/activate
echo 'Install to task-service'
pip3 uninstall -y fl-model
pip3 install --no-cache-dir --no-index --find-links fl-model-0.0.1.tar.gz fl-model


