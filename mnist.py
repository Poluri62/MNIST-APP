# -*- coding: utf-8 -*-
"""mnist.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1IpEnuG-NoOMHi1otYPiz3wuoZSn5k6mV
"""

"""from google.colab import drive
drive.mount('/content/gdrive')
!ln -s /content/gdrive/My\ Drive/ /mydrive
!ls /mydrive"""

# Commented out IPython magic to ensure Python compatibility.
# %matplotlib inline

import tensorflow as tf
from tensorflow import keras
from tensorflow.compat.v1.keras import backend as K

from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

import os
import os.path as path
import numpy as np

mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()

x_train, x_test = x_train/255, x_test/255
x_train = np.reshape(x_train,(60000,28,28,1))
x_test = np.reshape(x_test,(10000,28,28,1))

model = tf.keras.Sequential([
                             tf.keras.layers.Conv2D(16, (3,3),activation='relu', input_shape=(28,28,1)),
                             tf.keras.layers.MaxPool2D(2,2),
                             tf.keras.layers.Conv2D(16, (3,3),activation='relu', input_shape=(28,28,1)),
                             tf.keras.layers.MaxPool2D(2,2),
                             tf.keras.layers.Flatten(),
                             tf.keras.layers.Dense(512,activation = 'relu'),
                             tf.keras.layers.Dense(10,activation='softmax')
])
model.summary()

model.compile(
    loss = 'sparse_categorical_crossentropy',
    optimizer = tf.keras.optimizers.Adam(),
    metrics = ['accuracy']
)

model.fit(x_train, y_train, epochs=10)
# Save the entire model as a SavedModel.
#!mkdir "/mydrive/saved_model"
os.mkdir('saved_model')
model.save('saved_model')
