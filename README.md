1. **The Machine Learning Model (Python / TensorFlow):** * Trained a Convolutional Neural Network (CNN) using Transfer Learning (`MobileNetV2`).
   * Trained on the comprehensive 50,000+ image *PlantVillage* dataset.
   * Capable of classifying multiple crop diseases (e.g., Tomato Early Blight, Potato Late Blight) with high confidence.
2. **The Backend API (Java / Spring Boot):** * A scalable REST API built to handle multipart file uploads.
   * Integrated the **Deep Java Library (DJL)** to load and serve the TensorFlow `.pb` model natively in Java, eliminating the need for a separate Python Flask/FastAPI microservice.
   * Handles real-time image resizing and tensor translation (`uint8` to `float32`).
3. **The Deployment (Docker / Cloud):**
   * Containerized the application using a multi-stage `Dockerfile`.
   * Deployed asynchronously to the cloud (Render) for 24/7 API availability.
