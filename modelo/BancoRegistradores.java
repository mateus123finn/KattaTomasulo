package modelo;
public class BancoRegistradores {
    private int registradores[] = new int[32];
    private boolean acessoReg[] = new boolean[32];

    public BancoRegistradores(){

    }

    public int getValorReg(int numRegistrador){
        if(!acessoReg[numRegistrador]){
            acessoReg[numRegistrador] = true;
            return registradores[numRegistrador];
        }
        return -1;
    }

    public void escreveReg(int numRegistrador, int valor){
        acessoReg[numRegistrador] = false;
        registradores[numRegistrador] = valor;
    }


}
