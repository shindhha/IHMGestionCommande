package modeles;

public class CommandeFactory {
    String noCommande;
    String noLigne;
    int nbArticle;
    Article article;
    public CommandeFactory(String numero, Article article, int quantiteMax, String ligne) {
        this.noCommande = numero;
        this.article = article;
        this.nbArticle = quantiteMax;
        this.noLigne = ligne;
    }
    public Commande createCommande(String type) {
        if (type.equals("Ordre de Fabrication")) return new OF(noCommande, article, nbArticle, noLigne);
        else if (type.equals("Ordre de Commande")) return new OC(noCommande, article, nbArticle, noLigne);
        else throw new IllegalArgumentException("Le type de commande n'est pas reconnu");
    }
}
