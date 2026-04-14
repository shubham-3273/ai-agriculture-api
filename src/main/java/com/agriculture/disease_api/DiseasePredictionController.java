package com.agriculture.disease_api;

import ai.djl.modality.cv.transform.Resize;
import ai.djl.ndarray.types.DataType;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/api/disease")
@CrossOrigin(origins = "*") // Allows a web frontend to talk to this API
public class DiseasePredictionController {

    @PostMapping("/predict")
    public ResponseEntity<String> predictDisease(@RequestParam("image") MultipartFile file) {

        try {
            // 1. Convert the uploaded Spring Boot file into a DJL Image
            Image img = ImageFactory.getInstance().fromInputStream(file.getInputStream());

            // 2. Setup the Translator
            ImageClassificationTranslator translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(224, 224)) // Shrink image to match Colab training size
                    .addTransform(a -> a.toType(DataType.FLOAT32, false)) // Convert whole numbers to decimals (floats)
                    // IMPORTANT: Keep your exact list of classes here!
                    .optSynset(List.of("Pepper__bell___Bacterial_spot", "Pepper__bell___healthy", "Potato___Early_blight", "Potato___Late_blight", "Potato___healthy", "Tomato_Bacterial_spot", "Tomato_Early_blight", "Tomato_Late_blight", "Tomato_Leaf_Mold", "Tomato_Septoria_leaf_spot", "Tomato_Spider_mites_Two_spotted_spider_mite", "Tomato__Target_Spot", "Tomato__Tomato_YellowLeaf__Curl_Virus", "Tomato__Tomato_mosaic_virus", "Tomato_healthy"))
                    .build();

            // 3. Point Java to your downloaded AI Model
            Criteria<Image, Classifications> criteria = Criteria.builder()
                    .setTypes(Image.class, Classifications.class)
                    // IMPORTANT: Change this to the exact folder path where you unzipped your model
                    .optModelPath(Paths.get("AI_model"))
                    .optTranslator(translator)
                    .build();

            // 4. Load the model and make a prediction
            try (ZooModel<Image, Classifications> model = criteria.loadModel();
                 Predictor<Image, Classifications> predictor = model.newPredictor()) {

                Classifications result = predictor.predict(img);

                // Get the top prediction
                String diseaseName = result.best().getClassName();
                double confidence = result.best().getProbability() * 100;

                String response = String.format("Prediction: %s | Confidence: %.2f%%", diseaseName, confidence);
                return ResponseEntity.ok(response);
            }

        } catch (IOException | ModelException | TranslateException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error analyzing the image: " + e.getMessage());
        }
    }
}
