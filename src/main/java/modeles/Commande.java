package modeles;

import exceptions.FormatInvalideException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Commande {

    protected String numero;
    protected Article article;
    protected int quantiteMax;
    protected String date;
    protected ArrayList<String> listNumeroSerie;

    private Pattern pattern;
    public abstract void makeOutPutFile(String pathOutPutFolder) throws IOException;

    public Commande(String numero, Article article, int quantiteMax) {
        if (numero.isEmpty()) throw new IllegalStateException("Veuillez saisir le numéro de commande .");
        pattern = Pattern.compile(article.getFormat());
        this.numero = numero;
        this.article = article;
        this.quantiteMax = quantiteMax;
        this.listNumeroSerie = new ArrayList<>();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        this.date = dateFormat.format(date);

    }

    public String ajouterNumeroSerie(String numeroSerie) throws FormatInvalideException, IllegalStateException {
        if (listNumeroSerie.size() >= quantiteMax) throw new IllegalStateException("Vous avez atteint le nombre maximum d'article à saisir !");
        Matcher matcher = pattern.matcher(numeroSerie);
        if (!matcher.find()) throw new FormatInvalideException();
        String effectiveNumeroSerie = matcher.group();
        if (listNumeroSerie.contains(effectiveNumeroSerie)) throw new IllegalStateException("Le numéro de série a déjà été saisi !");

        listNumeroSerie.add(effectiveNumeroSerie);
        return effectiveNumeroSerie;
    }

    public int getNbMaxNumeroSerie() {
        return quantiteMax;
    }
    public int size() {
        return listNumeroSerie.size();
    }

    public void remove(String numeroSerie) {
        listNumeroSerie.remove(numeroSerie);
    }
    public void clear() {
        listNumeroSerie.clear();
    }
    public abstract String getFileOutPutName();

}
