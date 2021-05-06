/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Miguel Matul <https://github.com/MigueMat4>
 */
public class frmMain extends javax.swing.JFrame {
    
    private Uno generador;
    private String[] ordenJugadores;
    private String[] ordenTiempos;
    private String[] ordenLugares;
    private int contadorJugadores;
    private Jugador player1;
    private Jugador player2;
    private Jugador player3;
    String[] columnNames = {"Puesto", "Jugador", "Tiempo"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    //Implementacion de semaforo
    private static Semaphore mutex = new Semaphore(1, true);
    Thread HiloA;
    /**
     * Creates new form frmMain
     */
    public frmMain() {
        initComponents();
        tblResultados.setModel(model);
        lblCarta.setText("");
        generador = new Uno();
        player1 = new Jugador(1);
        player2 = new Jugador(2);
        player3 = new Jugador(3);
    }
    
    public class Jugador extends Thread {
        private final int numero;
        private boolean intento = false;
        
        public Jugador(int num) {
            numero = num;
            Image img = new ImageIcon(this.getClass().getResource("/img/Player-base.png")).getImage();
            if (numero == 1) {
                img = img.getScaledInstance(140, 140,  java.awt.Image.SCALE_SMOOTH);
                lblJugador1.setIcon(new ImageIcon(img));
            }
            if (numero == 2) {
                img = new ImageIcon(this.getClass().getResource("/img/Player-base-inverted.png")).getImage();
                img = img.getScaledInstance(140, 140,  java.awt.Image.SCALE_SMOOTH);
                lblJugador2.setIcon(new ImageIcon(img));
            }
            if (numero == 3) {
                img = img.getScaledInstance(140, 140,  java.awt.Image.SCALE_SMOOTH);
                lblJugador3.setIcon(new ImageIcon(img));
            }
        }
        
        @Override
        public void run(){
            try {
                mutex.acquire();
            System.out.println("Jugador " + this.numero + " listo para jugar");
            System.out.println("Jugador " + this.numero + " está atento al mazo");
            while(!intento) {
                if (generador.getNumeroCarta() == 5) {
                    manotazo();
                    System.out.println("Jugador " + this.numero + " entrando a la región crítica");
                    ordenJugadores[contadorJugadores] = "Jugador " + String.valueOf(numero);
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    ordenTiempos[contadorJugadores] = dateFormat.format(date);
                    ordenLugares[contadorJugadores] = String.valueOf(contadorJugadores+1) + " lugar";
                    contadorJugadores++;
                    System.out.println("Jugador " + this.numero + " saliendo de la región crítica");
                    intento = true;
                    System.out.println("Jugador " + this.numero + " esperando resultados");
                }
                try {
                    Thread.sleep(100);
                    //mutex.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                mutex.release();
            }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        public void manotazo() {
            Image img = new ImageIcon(this.getClass().getResource("/img/P"+String.valueOf(numero)+"-playing.png")).getImage();
            img = img.getScaledInstance(140, 140,  java.awt.Image.SCALE_SMOOTH);
            if (numero == 1)
                lblJugador1.setIcon(new ImageIcon(img));
            if (numero == 2)
                lblJugador2.setIcon(new ImageIcon(img));
            if (numero == 3)
                lblJugador3.setIcon(new ImageIcon(img));
        }
    }
    
    public class Uno extends Thread {
        private int numeroCarta;
        
        public Uno(){
            numeroCarta = 0;
            ordenJugadores = new String[3];
            ordenTiempos = new String[3];
            ordenLugares = new String[3];
            contadorJugadores = 0;
            // Cartas
            lblCarta.setText("");
            Image img = new ImageIcon(this.getClass().getResource("/cards/0.png")).getImage();
            img = img.getScaledInstance(93, 138,  java.awt.Image.SCALE_SMOOTH);
            lblCarta.setIcon(new ImageIcon(img));
            // Tabla
            model = new DefaultTableModel(columnNames, 0);
        }
        
    
        @Override    
        public void run() {
            try {
                mutex.acquire();
            System.out.println("--------------INICIO------------------------");
            System.out.println("Preparando el juego");
            System.out.println("Barajeando...");
            System.out.println("Mazo listo para el juego");
            btnJugar.setText("Jugando...");
           
            while(numeroCarta != 5) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                numeroCarta = (int)(Math.random() * 10 + 0);
                Image img = new ImageIcon(this.getClass().getResource("/cards/"+ String.valueOf(numeroCarta) +".png")).getImage();
                img = img.getScaledInstance(93, 138,  java.awt.Image.SCALE_SMOOTH);
                lblCarta.setIcon(new ImageIcon(img));
                
                mutex.release();
            }
            try {
                Thread.sleep(2000);
                System.out.println("Calculando resultados...");
            } catch (InterruptedException ex) {
                Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(int i=0; i<3; i++){
                Object[] rowdata;
                rowdata = new Object[] {ordenLugares[i], ordenJugadores[i], ordenTiempos[i]};
                model.addRow(rowdata);
            }
            
            tblResultados.setModel(model);
            btnJugar.setText("Jugar de nuevo");
            btnJugar.setEnabled(true);
            System.out.println("--------------FIN------------------------\n");
            
           }catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
   
        
        public int getNumeroCarta() {
            return numeroCarta;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblJugador1 = new javax.swing.JLabel();
        lblJugador2 = new javax.swing.JLabel();
        lblJugador3 = new javax.swing.JLabel();
        lblCarta = new javax.swing.JLabel();
        btnJugar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResultados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Evaluación Final - Sistemas Operativos");

        lblJugador1.setText("Jugador 1");

        lblJugador2.setText("Jugador 2");

        lblJugador3.setText("Jugador 3");

        lblCarta.setText("jLabel2");

        btnJugar.setText("Jugar");
        btnJugar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJugarActionPerformed(evt);
            }
        });

        tblResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblResultados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(lblJugador2)
                .addGap(76, 76, 76)
                .addComponent(lblCarta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJugador3)
                .addGap(570, 570, 570))
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addComponent(lblJugador1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 392, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(378, 378, 378)
                        .addComponent(btnJugar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblJugador1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblJugador3)
                            .addComponent(lblJugador2)
                            .addComponent(lblCarta))
                        .addGap(71, 71, 71)
                        .addComponent(btnJugar)
                        .addGap(139, 139, 139))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(107, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnJugarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugarActionPerformed
        generador = new Uno();
        //generador.comenzar_juego();
        //HiloA.start();
        player1.start();
        player2.start();
        player3.start();
        btnJugar.setEnabled(false);
    }//GEN-LAST:event_btnJugarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnJugar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCarta;
    private javax.swing.JLabel lblJugador1;
    private javax.swing.JLabel lblJugador2;
    private javax.swing.JLabel lblJugador3;
    private javax.swing.JTable tblResultados;
    // End of variables declaration//GEN-END:variables
}
