package modeles;

public class CommandeFactory {
    String noCommande;

    int nbArticle;
    Article article;
    public CommandeFactory(String numero, Article article, int quantiteMax) {
        if (article == null) throw new IllegalArgumentException("Veuillez renseigner un article");
        if (numero == null || numero.isEmpty()) throw new IllegalArgumentException("Veuillez renseigner un numéro de commande");
        this.noCommande = numero;
        this.article = article;
        this.nbArticle = quantiteMax;

    }
    public Commande createCommande(String type) {
        if (type.equals("Ordre de Fabrication")) return new OF(noCommande, article, nbArticle);
        else if (type.equals("Ordre de Commande")) return new OC(noCommande, article, nbArticle);
        else throw new IllegalArgumentException("Le type de commande n'est pas reconnu");
    }
}
