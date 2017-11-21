package homework3;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Mongo {

    public void removeLowest() {
        MongoClient mongoClient= new MongoClient();
        MongoDatabase schoolDB= mongoClient.getDatabase("school");
        MongoCollection<Document> students = schoolDB.getCollection("students");


        try(MongoCursor<Document> cursor = students.find().iterator()) {
            while (cursor.hasNext()) {
                Document student = cursor.next();
                List<Document> scores = (List<Document>) student.get("scores");
                Document minScoreObj = null;
                double minScore = Double.MAX_VALUE;

                for (Document singleScore : scores) {
                    double score = singleScore.getDouble("score");
                    String type = singleScore.getString("type");

                    System.out.println(singleScore);
                    if (type.equals("homework") && score < minScore) {
                        minScore = score;
                        minScoreObj = singleScore;

                    }
                }

                if (minScoreObj != null) {
                    scores.remove(minScoreObj);
                }
                students.updateOne(eq("_id", student.get("_id")),
                        new Document("$set", new Document("scores", scores)));
            }

        }
        mongoClient.close();
    }

}
