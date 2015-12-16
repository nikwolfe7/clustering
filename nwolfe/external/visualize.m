function visualize(fileName, data, label)
figure('Visible','Off')
for i = 1:size(data, 1)
    if label(i, 1) == 1
        plot(data(i, 1), data(i, 2), 's', 'color', 'red', 'linewidth', 1);hold on
    elseif label(i, 1) == 2
        plot(data(i, 1), data(i, 2), 'o', 'color', 'green', 'linewidth', 1);hold on
    elseif label(i, 1) == 3
        plot(data(i, 1), data(i, 2), '+', 'color', 'blue', 'linewidth', 1);hold on
    elseif label(i, 1) == 4
        plot(data(i, 1), data(i, 2), 'v', 'color', 'yellow', 'linewidth', 1);hold on
    elseif label(i, 1) == 5
        plot(data(i, 1), data(i, 2), '^', 'color', 'cyan', 'linewidth', 1);hold on
    elseif label(i, 1) == 6
        plot(data(i, 1), data(i, 2), 'x', 'color', 'magenta', 'linewidth', 1, 'markersize', 8);hold on
    else
        plot(data(i, 1), data(i, 2), '<', 'color', 'black', 'linewidth', 1);hold on
    end
end
hold off
export_fig(fileName)