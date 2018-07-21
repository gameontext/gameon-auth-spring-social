FROM gameontext/docker-liberty-custom

ENV SERVERDIRNAME auth-spring-social

COPY . /
HEALTHCHECK --timeout=10s --start-period=40s CMD wget -q -O /dev/null http://localhost:9080/health

CMD ["/startup.sh"]
