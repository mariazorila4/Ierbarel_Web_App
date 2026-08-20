from flask import Flask, request, jsonify
from ultralytics import YOLO
import requests
from PIL import Image
import io

app=Flask(__name__)
model=YOLO('yolov8n.pt')

@app.route('/scan', methods=['POST'])
def scan():
    if 'file' not in request.files:
        return jsonify({'error':'Nu a fost trimis niciun fisier'}), 400

        file=resquest.files['file']
        if file.filename=='':
            return jsonify({'error':'Fisier selectat invalid'}), 400

        try:
            image_byes=file.read()
            image=Image.open(io.BytesIO(image_bytes))

            results=model(image)

            if len(results)>0 and len(results[0].boxes)>0:
                top_class_id=int(results[0].boxes[0].cls[0])
                plant_name=results[0].names[top_class_id]
                confidence=float(results[0].boxes[0].conf[0])

                return jsonify({'nume_planta':planta_name, 'confidenta':confidence})
            else:
                return jsonify({'nume_planta':'Necunoscuta', 'confidenta':0.0}), 500

        except Exception as e:
            return jsonify({'error':f'Eroare la procesare imagine: {str(e)}'}), 500

    if __name__=='__main__':
        app.run(host='0.0.0.0', port=5000, debug=True)