package com.covoit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notations")
public class Notation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evaluateur_id", nullable = false)
    private Utilisateur evaluateur;

    @ManyToOne
    @JoinColumn(name = "evalue_id", nullable = false)
    private Utilisateur evalue;

    @Column(nullable = false)
    private int note; // De 1 à 5

    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "trajet_id")
    private Trajet trajet;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getEvaluateur() { return evaluateur; }
    public void setEvaluateur(Utilisateur evaluateur) { this.evaluateur = evaluateur; }
    public Utilisateur getEvalue() { return evalue; }
    public void setEvalue(Utilisateur evalue) { this.evalue = evalue; }
    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Trajet getTrajet() { return trajet; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }
}
