package ch.hevs.datasemlab.cityzen.ch.hevs.datasemlab.cityzen.AsyncTasksForQueries;

import android.os.AsyncTask;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import ch.hevs.datasemlab.cityzen.CityzenContracts;

/**
 * Created by Alex on 9/4/2016.
 */
public class GetCulturalInterestsNumberAsyncTask extends AsyncTask<String, Void, Integer>{

    public interface AsyncResponse {
        void processFinish(Integer output);
    }

    private AsyncResponse delegate = null;

    public GetCulturalInterestsNumberAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected Integer doInBackground(String... strings) {

        Repository repo = new SPARQLRepository(CityzenContracts.REPOSITORY_URL);
        repo.initialize();

        RepositoryConnection conn = repo.getConnection();

        String title = strings[0];

        TupleQueryResult result = null;

        try {
            StringBuilder qb = new StringBuilder();

            qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
            qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");

            qb.append(" SELECT (COUNT(DISTINCT ?culturalInterest) AS ?count) \n ");

            qb.append(" WHERE {?culturalInterest dc:title ");
            qb.append("\"" + title + "\" . \n } ");

            result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

        } finally {
            conn.close();
        }

        Integer count = Integer.valueOf(result.next().getBinding("count").getValue().stringValue());

        return count;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        delegate.processFinish(result);
    }
}
