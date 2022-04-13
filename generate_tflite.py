import tensorflow as tf

#Convert the model
converter = tf.lite.TFLiteConverter.from_saved_model('saved_model')
tflite_model = converter.convert()

#Saving model
with open('model.tflite', 'wb') as f:
    f.write(tflite_model)