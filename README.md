Auth Troubles
=============

The app has an endpoint at `/api/foo` secured by basic auth and a page at `/home` secured by OAuth/OIDC.

You can

    ./gradlew bootRun

then some things work

    curl -v http://localhost:8080/home # => 302, as expected
    curl -v http://localhost:8080/api/foo # => 401, as expected
    curl -v http://username:password@localhost:8080/api/foo # => 200, as expected
    curl -v http://guest:password@localhost:8080/api/foo # => 403, as expected

but some things break confusingly

    curl -v http://asdf:asdf@localhost:8080/api/foo # => 302 instead of 401
    curl -v http://username:wrong@localhost:8080/api/foo # => 302 instead of 401

but if you comment out `OktaWebConfiguration` then the 401/403s we expect are returned.

You can also

    ./gradlew check

and see our assertions pass, even though we observe different behavior in real life.