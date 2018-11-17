Y = lambda f: (lambda g: g(g))(lambda h: lambda *args: f(h(h))(*args))


def fac_gen(q):
    def g(n, m):
        if n % m == 0:
            print (m)
        else:
            q(m, n % m)
    return g

Y(fac_gen)(81, 18)
