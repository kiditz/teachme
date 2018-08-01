FROM teachme/python3
MAINTAINER Rifky aditya bastara "kiditzbastara@gmail.com"


EXPOSE 5002

WORKDIR app
#RUN apt install --no-cache --update-cache add uwsgi-python3 tzdata
#ENV TZ=Asia/Jakarta
#RUN date


RUN apt-get install -y --no-install-recommends apt-utils
RUN apt-get install -y uwsgi uwsgi-plugin-python3 python3-dev
COPY /requirements.txt ./requirements.txt
RUN pip3 install -r requirements.txt
COPY ./app /app
ENV PYTHONPATH=/usr/local/lib/python3.6/site-packages
ENV CONSUL_HOST=172.17.0.1
ENV CONSUL_PORT=8500
RUN groupadd -r uwsgi && useradd -r -g uwsgi uwsgi
RUN apt-get install -y libssl-dev
RUN mv /usr/local/lib/python3.6/site-packages/_cffi_backend.cpython-36m-x86_64-linux-gnu.so  /usr/local/lib/python3.6/site-packages/_cffi_backend
CMD [ "uwsgi", "--socket", "0.0.0.0:5002", "--processes", "2","--threads", "10", "--plugins", "python35", "--protocol=http", "-w", "main:app" ]
