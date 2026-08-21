import sys
print("1. Inițializare script Flask...", flush=True)

from flask import Flask, request, jsonify
from PIL import Image
import io

print("2. Se încarcă modulul Ultralytics (poate dura câteva secunde)...", flush=True)
from ultralytics import YOLO

app = Flask(__name__)

print("3. Se încarcă modelul best.pt...", flush=True)
model = YOLO('best.pt') 
print("✅ Modelul best.pt a fost încărcat cu succes!", flush=True)

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'Niciun fișier trimis'}), 400

    file = request.files['file']
    try:
        image_bytes = file.read()
        image = Image.open(io.BytesIO(image_bytes))

        results = model(image)

        if len(results) > 0 and len(results[0].boxes) > 0:
            top_class_id = int(results[0].boxes[0].cls[0])
            plant_name = results[0].names[top_class_id]
            print(f"Detectat: {plant_name}", flush=True)
            return jsonify({'clasa': plant_name})
        else:
            return jsonify({'clasa': 'Necunoscuta'})

    except Exception as e:
        print(f"Eroare: {str(e)}", flush=True)
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    print("4. Se pornește serverul pe portul 5000...", flush=True)
    app.run(host='0.0.0.0', port=5000, debug=True)