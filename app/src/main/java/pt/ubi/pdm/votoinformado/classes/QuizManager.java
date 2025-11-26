package pt.ubi.pdm.votoinformado.classes;

import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private double currentEconomicScore = 0;
    private double currentSocialScore = 0;
    private double maxEconomicScore = 0;
    private double maxSocialScore = 0;

    public QuizManager() {
        questions = new ArrayList<>();
        loadQuestions();
    }

    private void loadQuestions() {
        // Economic Questions (Left vs Right)
        questions.add(new Question("O governo deve controlar a economia para garantir a igualdade.", -1.0, 0.0));
        questions.add(new Question("O mercado livre é a melhor forma de distribuir recursos.", 1.0, 0.0));
        questions.add(new Question("Os ricos devem pagar impostos significativamente mais altos.", -1.0, 0.0));
        questions.add(new Question("A privatização de serviços públicos é benéfica.", 1.0, 0.0));
        
        // Social Questions (Libertarian vs Authoritarian)
        questions.add(new Question("A autoridade deve ser sempre respeitada.", 0.0, 1.0));
        questions.add(new Question("As liberdades individuais são mais importantes que a ordem social.", 0.0, -1.0));
        questions.add(new Question("O estado deve promover valores tradicionais.", 0.0, 1.0));
        questions.add(new Question("O que dois adultos consentem em privado não é da conta do estado.", 0.0, -1.0));
        
        // Mixed Questions
        questions.add(new Question("A proteção ambiental é mais importante que o crescimento económico.", -0.5, -0.5));
        questions.add(new Question("A segurança nacional justifica a vigilância dos cidadãos.", 0.0, 1.0));

        // New Questions
        questions.add(new Question("O acesso à saúde deve ser totalmente gratuito e público.", -1.0, 0.0));
        questions.add(new Question("As escolas privadas oferecem melhor educação que as públicas.", 0.5, 0.0));
        questions.add(new Question("A imigração enriquece a nossa cultura.", 0.0, -1.0));
        questions.add(new Question("Devemos priorizar os interesses nacionais sobre a cooperação internacional.", 0.0, 1.0));
        questions.add(new Question("O aborto deve ser legal e acessível.", 0.0, -1.0));
        questions.add(new Question("A religião deve ter um papel na vida pública.", 0.0, 1.0));
        questions.add(new Question("Os sindicatos são essenciais para proteger os trabalhadores.", -1.0, 0.0));
        questions.add(new Question("A carga fiscal sobre as empresas deve ser reduzida.", 1.0, 0.0));

        // Calculate max possible scores for normalization
        for (Question q : questions) {
            maxEconomicScore += Math.abs(q.getEconomicWeight());
            maxSocialScore += Math.abs(q.getSocialWeight());
        }
    }

    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public void answerQuestion(double multiplier) {
        // multiplier: 1.0 (Strongly Agree), 0.5 (Agree), -0.5 (Disagree), -1.0 (Strongly Disagree)
        Question q = getCurrentQuestion();
        if (q != null) {
            currentEconomicScore += q.getEconomicWeight() * multiplier;
            currentSocialScore += q.getSocialWeight() * multiplier;
            currentQuestionIndex++;
        }
    }

    public boolean isFinished() {
        return currentQuestionIndex >= questions.size();
    }

    public double getFinalEconomicScore() {
        // Normalize to -10 to 10 range
        if (maxEconomicScore == 0) return 0;
        return (currentEconomicScore / maxEconomicScore) * 10.0;
    }

    public double getFinalSocialScore() {
        // Normalize to -10 to 10 range
        if (maxSocialScore == 0) return 0;
        return (currentSocialScore / maxSocialScore) * 10.0;
    }
}
