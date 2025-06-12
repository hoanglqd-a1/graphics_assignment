import numpy as np

a = np.array([[0.0,  0.622008459, 0.0],
              [-0.5, -0.311004243, 0.0],
              [0.5, -0.311004243, 0.0]])

p = np.array([0.0, 0.0, 0.0])
a0a1 = a[1] - a[0]
a0p = p - a[0]
print(np.cross(a0a1, a0p))