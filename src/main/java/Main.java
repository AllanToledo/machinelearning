import machinelearning.DecisionTree;
import machinelearning.NeuralNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public enum Personagens {
        HOMER, BART, LISA, MARGE, MAGGIE, NELSON
    }


    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        List<Map<String, Double>> data = loadData("src\\main\\resources\\personagens-v1.csv");
        String attributeClass = "personagem";

        Map<String, Double> example = new HashMap<>();
        //LISA
        example.put("idade", 13.0);
        example.put("altura", 158.0);
        example.put("peso", 40.0);
        example.put("sexo", 1.0);
        //BART
//        example.put("idade", 13.0);
//        example.put("altura", 158.0);
//        example.put("peso", 40.0);
//        example.put("sexo", 0.0);
        //HOMER
//        example.put("idade", 46.0);
//        example.put("altura", 168.0);
//        example.put("peso", 162.0);
//        example.put("sexo", 0.0);

        Personagens personagem;

        DecisionTree decisionTree = new DecisionTree();
        //o segundo parametro é o atributo que ele vai aprender a classificar
        decisionTree.fit(data, attributeClass);
        int decisionTreeResult = decisionTree.eval(example);
        personagem = Personagens.values()[decisionTreeResult];
        System.out.printf("Arvore de decisão: %s\n", personagem);
//        System.out.println(decisionTree);

        //Rede neural funcionou melhor com os valores normalizados de 0 até 1
        //Para normalizar o valor é divido pelo valor máximo na base de dados
        Map<String, Double> maxValues = getMaxValues(data);
//        System.out.println(maxValues);
//        System.out.println(maxValues.keySet());
        normalize(data, maxValues, attributeClass);
        normalize(example, maxValues, attributeClass);
//        System.out.println(example);
        //Criando a Rede Neural para classificação
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        //Cria a matriz com o número de entradas (atributos) e a quantidade de neuronios para classificação
        neuralNetwork.build(data, attributeClass);
//        System.out.println(neuralNetwork);
        neuralNetwork.learningRate = 0.1;
        neuralNetwork.fit(data, 100);

        int neuralNetworkResult = neuralNetwork.eval(example);
//        System.out.println(neuralNetwork);
//        System.out.println(neuralNetwork.processInputs(example));
//        System.out.println(neuralNetwork.applyActivactionFunction(neuralNetwork.processInputs(example)));
        personagem = Personagens.values()[neuralNetworkResult];
        System.out.printf("Rede Neural: %s\n", personagem);
//        System.out.println(neuralNetwork);

    }

    private static Map<String, Double> getMaxValues(List<Map<String, Double>> data) {
        return data.stream().reduce(new HashMap<>(), (e, r) -> {
            for (var prop : r.entrySet()) {
                e.put(prop.getKey(), Math.max(prop.getValue(), e.getOrDefault(prop.getKey(), 0.0)));
            }
            return e;
        });
    }

    private static void normalize(List<Map<String, Double>> data,
                                  Map<String, Double> maxValues,
                                  String exclude) {
        for (var e : data) normalize(e, maxValues, exclude);
    }

    private static void normalize(Map<String, Double> e, Map<String, Double> maxValues, String exclude) {
        for (String key : e.keySet()) {
            if (key.equals(exclude)) continue;
            e.put(key, e.get(key) / maxValues.get(key));
        }
    }

    private static List<Map<String, Double>> loadData(String path) throws FileNotFoundException {
        List<Map<String, Double>> data = new ArrayList<>();
        Scanner sc = new Scanner(new File(path));
        List<String> labels = new ArrayList<>(List.of(sc.nextLine().split(",")));
        while (sc.hasNext()) {
            String[] values = sc.nextLine().split(",");
            Map<String, Double> row = new HashMap<>();
            for (int i = 0; i < labels.size(); i++)
                row.put(labels.get(i), Double.parseDouble(values[i]));
            data.add(row);
        }
        sc.close();
        return data;
    }
}
