import csv, math
from collections import defaultdict
import matplotlib.pyplot as plt
rows=list(csv.DictReader(open('results/results.csv')))
for r in rows:
 for k in ('n','m','edge_scans','dsu_ops','comparisons'):r[k]=int(r[k])
fig,ax=plt.subplots(figsize=(9,6))
for (alg,density),rs in __import__('itertools').groupby(sorted(rows,key=lambda r:(r['algorithm'],r['m']//r['n'],r['n'])),key=lambda r:(r['algorithm'],r['m']//r['n'])):
 rs=list(rs);ax.plot([r['n'] for r in rs],[max(1,r['edge_scans']+r['dsu_ops']+r['comparisons']) for r in rs],marker='o',label=f'{alg} {density}n')
ax.set(xlabel='n',ylabel='Counted operations',title='Operations vs n');ax.set_xscale('log');ax.set_yscale('log');ax.legend(fontsize=7,ncol=2);fig.tight_layout();fig.savefig('results/plots/operations.png');plt.close(fig)
fig,ax=plt.subplots(figsize=(9,6))
for (alg,density),rs in __import__('itertools').groupby(sorted(rows,key=lambda r:(r['algorithm'],r['m']//r['n'],r['n'])),key=lambda r:(r['algorithm'],r['m']//r['n'])):
 rs=list(rs);ax.plot([r['n'] for r in rs],[r['edge_scans']/r['m'] for r in rs],marker='o',label=f'{alg} {density}n')
ax.set(xlabel='n',ylabel='Edge scans / m',title='Scans per edge');ax.legend(fontsize=7,ncol=2);fig.tight_layout();fig.savefig('results/plots/scans_per_edge.png');plt.close(fig)
fig,ax=plt.subplots(figsize=(9,6))
for density in (4,20):
 rs=sorted([r for r in rows if r['algorithm']=='Kruskal' and r['m']//r['n']==density],key=lambda r:r['n']);ax.plot([r['n'] for r in rs],[r['edge_scans']/r['m'] for r in rs],marker='o',label=f'm={density}n')
ax.set(xlabel='n',ylabel='Fraction of edges examined',title='Kruskal scanned edges / m');ax.legend();fig.tight_layout();fig.savefig('results/plots/kruskal_share.png');plt.close(fig)
