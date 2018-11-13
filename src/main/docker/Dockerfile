FROM gameontext/docker-liberty-custom:master-14

ENV SERVERDIRNAME auth-spring-social

COPY . /
HEALTHCHECK --timeout=10s --start-period=40s CMD wget -q -O /dev/null http://localhost:9080/auth/health

CMD ["/startup.sh"]
