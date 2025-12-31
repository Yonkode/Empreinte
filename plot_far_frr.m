% ================================
% FAR / FRR / ROC / EER PLOT
% ================================

clear; clc; close all;

% Charger les données
data = readtable('roc_db1.csv');

threshold = data.threshold;
FAR = data.FAR;
FRR = data.FRR;

% ================================
% Calcul EER
% ================================
[~, idx] = min(abs(FAR - FRR));
eer = (FAR(idx) + FRR(idx)) / 2;
eer_threshold = threshold(idx);

fprintf('EER = %.2f %%\n', eer * 100);
fprintf('Seuil EER = %.3f\n', eer_threshold);

% ================================
% FAR / FRR vs Threshold
% ================================
figure;
plot(threshold, FAR, 'r-', 'LineWidth', 2); hold on;
plot(threshold, FRR, 'b-', 'LineWidth', 2);
plot(eer_threshold, eer, 'ko', 'MarkerFaceColor', 'k');

xlabel('Seuil');
ylabel('Taux');
title('FAR / FRR en fonction du seuil');
legend('FAR', 'FRR', 'EER', 'Location', 'best');
grid on;

% ================================
% ROC Curve
% ================================
figure;
plot(FAR, 1 - FRR, 'LineWidth', 2);
xlabel('False Acceptance Rate (FAR)');
ylabel('True Acceptance Rate (1 - FRR)');
title('Courbe ROC');
grid on;

% ================================
% DET Curve (optionnelle)
% ================================
figure;
semilogx(FAR, FRR, 'LineWidth', 2);
xlabel('FAR (log)');
ylabel('FRR');
title('Courbe DET');
grid on;
